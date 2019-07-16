package com.sfygroup.wechat.bot;

import com.google.gson.JsonParser;
import com.sfygroup.wechat.util.DateUtil;
import com.sfygroup.wechat.util.filter.ContentFilter;
import com.sfygroup.wechat.util.filter.DateContentFilter;
import com.sfygroup.wechat.util.sfyutils.YamUtils;
import io.github.biezhi.wechat.WeChatBot;
import io.github.biezhi.wechat.api.annotation.Bind;
import io.github.biezhi.wechat.api.constant.Config;
import io.github.biezhi.wechat.api.enums.AccountType;
import io.github.biezhi.wechat.api.enums.MsgType;
import io.github.biezhi.wechat.api.model.Account;
import io.github.biezhi.wechat.api.model.WeChatMessage;
import io.github.biezhi.wechat.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * 我的小机器人
 *
 * @author biezhi
 * @date 2018/1/19
 */
@Slf4j
@Data
public class MyBot extends WeChatBot {

    private Map<String, ContentFilter> filtersMap = new HashMap<String, ContentFilter>() {{
        put("日期", new DateContentFilter());
    }};

    public MyBot(Config config) {
        super(config);
    }


    private void mockGroupMessage() {
        List<Map> messages = (List<Map>) YamUtils.getProperty("groupMessages");
        for (Map map : messages) {
            WeChatMessage message = new WeChatMessage();
            try {
                Object fromNickName = map.get("fromNickName");
                if (fromNickName == null) {
                    log.error("未设置fromNickName,{}", map);
                    continue;
                }
                Account account = this.api().getAccountByName(fromNickName.toString());
                message.setFromNickName(account.getNickName());
                message.setFromRemarkName(account.getRemarkName());
                message.setFromUserName(account.getUserName());
                message.setAtMe(true);
                BeanUtils.populate(message, map);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("赋值失败" + map);
            }
            groupMessage(message);
        }
    }

    /**
     * 绑定群聊信息
     *
     * @param message
     */
    @Bind(msgType = MsgType.ALL, accountType = AccountType.TYPE_GROUP)
    public void groupMessage(WeChatMessage message) {
        try {
            String name = message.getName();
            String text = message.getText();
            if (!message.isAtMe()) {
                return;
            }

            log.info("接收到群 [{}] 的消息: {}", name, text);
            if (YamUtils.getStringProperty("community." + name) == null) {
                return;
            }

            String key;
            String[] split = text.split("\\|");
            if (split.length > 1) {
                //说明存在变化
                key = split[0];
            } else {
                key = text;
            }
            key = key.trim().replace(" ", "");
            String operation = YamUtils.getStringProperty("operation." + key);

            if (operation != null) {
                String filePath = "";
                //说明存在操作设置
                filePath = createMessage(operation, split);
                // 目前仅支持返回文件
                boolean b = this.api().sendImg(message.getFromUserName(), filePath);
                if (!b) {
                    log.info("发送文件失败");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    ScheduledExecutorService executorService = null;

    /**
     * 绑定私聊消息
     *
     * @param message
     */
    @Bind(msgType = {MsgType.TEXT, MsgType.VIDEO, MsgType.IMAGE, MsgType.EMOTICONS}, accountType = AccountType.TYPE_FRIEND)
    public void friendMessage(WeChatMessage message) {

        Account account = this.api().getAccountById(message.getFromUserName());
        String text = message.getText();
        String admin = YamUtils.getStringProperty("adminUser." + account.getNickName());
        if (admin == null) {
            admin = YamUtils.getStringProperty("adminUser." + account.getRemarkName());
        }
        if (admin != null) {
            if (text != null && YamUtils.getStringProperty("timer.operation.startup").equals(text)) {
                if (executorService != null) {
                    executorService.shutdown();
                    executorService = null;
                }
                executorService = new ScheduledThreadPoolExecutor(1,
                        new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

                String delay = YamUtils.getStringProperty("schedule.delay");
                String hour = YamUtils.getStringProperty("schedule.start.hour");
                String minute = YamUtils.getStringProperty("schedule.start.minute");
                if (delay == null || hour == null || minute == null) {
                    this.api().sendText(message.getFromUserName(), "未设置：schedule.start:minute【" + minute +
                            "】，hour【" + hour + "】，delay【" + delay + "】");
                    return;
                }
                Long delayLong = Long.valueOf(delay);

                Calendar instance = Calendar.getInstance();
                instance.set(Calendar.HOUR, Integer.valueOf(hour));
                instance.set(Calendar.MINUTE, Integer.valueOf(minute));
                instance.set(Calendar.SECOND, 0);
                Date time = instance.getTime();
                TimeUnit timeUnit = TimeUnit.valueOf(YamUtils.getStringProperty("schedule.unit"));
                // 当前时间
                long now = System.currentTimeMillis();
                // 默认时间
                long target = instance.getTimeInMillis();
                long convert = 0;
                long length = now - instance.getTimeInMillis();
                if (now > target) {
                    convert = timeUnit.convert(delayLong - length, timeUnit) / 1000;
                } else {
                    //计算超时多长时间
                    convert = timeUnit.convert(length, timeUnit) / 1000;
                    convert = convert % delayLong;
                }

                executorService.scheduleAtFixedRate(new TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            mockGroupMessage();
                                                        }
                                                    },
                        convert,
                        delayLong,
                        timeUnit
                );
                log.info(admin + "启动了定时器任务,第一次启动时间{},距今延迟{}", DateUtil.getStrDate(time, "yyyy-MM-dd hh:mm:ss"), convert);
            } else if (text != null && YamUtils.getStringProperty("timer.operation.shutdown").equals(text)) {
                if (executorService != null) {
                    executorService.shutdown();
                    executorService = null;
                    log.info(admin + "关闭了定时器任务");
                }
            }
        } else {
            log.info("收到非管理员消息:" + message);
        }
    }


    /**
     * 好友验证消息
     *
     * @param message
     */
    @Bind(msgType = MsgType.ADD_FRIEND)
    public void addFriend(WeChatMessage message) {
        log.info("收到好友验证消息: {}", message.getText());
        if (message.getText().contains("java")) {
            this.api().verify(message.getRaw().getRecommend());
        }
    }

    public static void main(String[] args) {
        MyBot bot = new MyBot(Config.me().autoLogin(true).showTerminal(true));
        new YamUtils(bot.config().assetsDir());
        bot.start();
    }

    /**
     * 匹配${}
     */
    private static Pattern PATTERN = Pattern.compile("\\$\\{.*?\\}");

    public String createMessage(String content, String[] split) {

        Matcher matcher = PATTERN.matcher(content);

        matcher.reset();
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group();
            String fieldDisplayName = group.substring(2, group.length() - 1);
            //判断有误中转编码
            String[] filters = fieldDisplayName.split(":");
            if (filters.length > 1) {
                //说明有中转编码
                fieldDisplayName = filters[0];
            }

            //获取数据库的值
            String value = fieldDisplayName;
            String stringProperty = YamUtils.getStringProperty(fieldDisplayName);
            if (stringProperty != null) {
                value = stringProperty;
            }
            if (filters.length > 1) {
                for (int i = 1; i < filters.length; i++) {
                    ContentFilter contentFilter = filtersMap.get(filters[i]);
                    //处理数据
                    value = contentFilter.editMessage(value);
                }
            }
            if ("suffix".equals(fieldDisplayName)) {
                value = split[1];
            }

            //返回
            if (StringUtils.isNotEmpty(value)) {
                matcher.appendReplacement(sb, value);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


}
