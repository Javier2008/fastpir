package com.pir.fastpir.common;

import lombok.Data;

/**
 * created by yyunf
 * 2023/8/15
 */
@Data
public class AjaxResult <T>
{
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    public static final String CODE_TAG = "code";

    /** 返回内容 */
    public static final String MSG_TAG = "msg";

    /** 数据对象 */
    public static final String DATA_TAG = "data";

    private int code;
    private String msg;
    private T data;

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public AjaxResult()
    {
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     */
    public AjaxResult(int code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }

    public AjaxResult(String code, String msg) {
        this.code = Integer.parseInt(code);
        this.msg = msg;
    }
    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public AjaxResult(int code, String msg, T data)
    {
        this.code = code;
        this.msg = msg;
        if (data != null)
        {
            this.data = data;
        }
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public AjaxResult(String code, String msg, T data)
    {
        this.code = Integer.parseInt(code);
        this.msg = msg;
        if (data != null) {
            this.data = data;
        }
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static <K> AjaxResult<K> success()
    {
        return AjaxResult.success("操作成功", null);
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static <K> AjaxResult<K> success(K data)
    {
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static <K> AjaxResult<K> success(String msg, K data)
    {
        return new AjaxResult<>(Status.SUCCESS, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return 警告消息
     */
    public static <K> AjaxResult<K> error()
    {
        return AjaxResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <K> AjaxResult<K> error(String msg)
    {
        return AjaxResult.error(Status.ERROR, msg);
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static <K> AjaxResult<K> error(String msg, K data)
    {
        return new AjaxResult<>(Status.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <K> AjaxResult<K> error(int code, String msg)
    {
        return new AjaxResult<>(code, msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static <K> AjaxResult<K> error(String code, String msg) {
        return new AjaxResult<>(code, msg);
    }
}
