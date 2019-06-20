package com.sfygroup.wechat.util.filter;


import com.sfygroup.wechat.util.DateUtil;

import java.util.Date;

public class DateContentFilter implements ContentFilter {
    @Override
    public String editMessage(String value) {
        String[] split = value.split("\\|");
        if ("now".equals(split[0])) {
            if (split.length > 1) {
                return DateUtil.getStrDate(new Date(), split[1]);
            } else {
                return DateUtil.getStrDate(new Date(), "yyyy-MM-dd");
            }
        } else {
            if (split.length > 1) {
                return DateUtil.getStrDate(new Date(split[0]), split[1]);
            } else {
                return DateUtil.getStrDate(new Date(split[0]), "yyyy-MM-dd");
            }
        }
    }
}
