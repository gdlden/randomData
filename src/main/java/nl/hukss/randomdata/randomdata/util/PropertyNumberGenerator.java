package nl.hukss.randomdata.randomdata.util;

import org.apache.commons.lang3.StringUtils;

import java.time.Year;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class PropertyNumberGenerator {
    private static final Map<String, String> PROVINCE_ABBREVIATIONS = Map.ofEntries(
            Map.entry("北京", "京"), Map.entry("天津", "津"), Map.entry("河北", "冀"),
            Map.entry("山西", "晋"), Map.entry("内蒙古", "蒙"), Map.entry("辽宁", "辽"),
            Map.entry("吉林", "吉"), Map.entry("黑龙江", "黑"), Map.entry("上海", "沪"),
            Map.entry("江苏", "苏"), Map.entry("浙江", "浙"), Map.entry("安徽", "皖"),
            Map.entry("福建", "闽"), Map.entry("江西", "赣"), Map.entry("山东", "鲁"),
            Map.entry("河南", "豫"), Map.entry("湖北", "鄂"), Map.entry("湖南", "湘"),
            Map.entry("广东", "粤"), Map.entry("广西", "桂"), Map.entry("海南", "琼"),
            Map.entry("重庆", "渝"), Map.entry("四川", "川"), Map.entry("贵州", "黔"),
            Map.entry("云南", "云"), Map.entry("西藏", "藏"), Map.entry("陕西", "陕"),
            Map.entry("甘肃", "甘"), Map.entry("青海", "青"), Map.entry("宁夏", "宁"),
            Map.entry("新疆", "新"), Map.entry("Chongqing", "渝")
    );

    private PropertyNumberGenerator() {
    }

    public static String randomFactoryBuildingNo() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        char zone = (char) ('A' + random.nextInt(8));
        int building = random.nextInt(1, 31);
        int unit = random.nextInt(1, 7);
        return String.format("%c区%02d栋-%02d号厂房", zone, building, unit);
    }

    public static String randomPropertyCertificateNo(String province, String city) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int currentYear = Year.now().getValue();
        int registrationYear = random.nextInt(Math.max(2016, currentYear - 10), currentYear + 1);
        int serialNumber = random.nextInt(1, 100_000_000);
        return String.format("%s（%d）%s不动产权第%08d号",
                provinceAbbreviation(province), registrationYear, normalizeCity(city), serialNumber);
    }

    private static String provinceAbbreviation(String province) {
        if (StringUtils.isBlank(province)) {
            return "渝";
        }
        return PROVINCE_ABBREVIATIONS.entrySet().stream()
                .filter(entry -> province.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("渝");
    }

    private static String normalizeCity(String city) {
        if (StringUtils.isBlank(city)) {
            return "市辖区";
        }
        if ("Chongqing".equalsIgnoreCase(city)) {
            return "重庆市";
        }
        if (city.matches(".*[\\u4e00-\\u9fa5].*")) {
            return city.endsWith("市") || city.endsWith("区") || city.endsWith("县") ? city : city + "市";
        }
        return "市辖区";
    }
}
