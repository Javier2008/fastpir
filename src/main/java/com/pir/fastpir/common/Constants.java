package com.pir.fastpir.common;

/**
 * 通用常量信息
 * 
 * @author pengbaoming
 */
public class Constants
{
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRATION = 2;

    /**
     * 令牌有效期（分钟）
     */
    public final static long TOKEN_EXPIRE = 720;

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    public static final String HEADER_DS_ID = "dsId";

    /**
     * 跳过验证码登陆的header标识
     */
    public static final String AUTH_HEADER_SING = "Sing";
    /**
     * 跳过验证码登陆的header标识value
     */
    public static final String AUTH_HEADER_SING_VALUE = "Auth";

    /**
     * 登陆路径
     */
    public final static String AUTH_URL = "/auth/login";

    /**
     * 登出路径
     */
    public final static String AUTH_URL_LOGOUT = "/auth/logout";

    /**
     * 用户信息
     */
    public final static String USER_INFO = "/system/user/info/";

    /**
     * 用户信息
     */
    public final static String SYS_PREFIX = "/system";

    /**
     * operator信息
     */
    public final static String OPERATOR_PREFIX = "/operator";

    /**
     * 合约信息
     */
    public final static String CONTRACT_PREFIX = "/contract";

    /**
     * console信息
     */
    public final static String CONSOLE_PREFIX = "/console";

    /**
     * 合约审核信息
     */
    public final static String CONTRACT_PPROVE_PREFIX = "/contractApprove";


    public static String PUBLIC_KEY_URL = "/auth/getPublicKey";

    public static final String HEALTH_URL= "/auth/health";

    public static final String REFRESH_URL= "/auth/refresh";
}
