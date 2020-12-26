package handler;

public enum RespState {

        DECODE_PARAM_COUNT,// 参数数量
        DECODE_PARAM_LENGTH,// 参数长度
        DECODE_PARAM_DATA,// 参数具体数据
        COMMAND_INTEGRATION//把参数整合成命令

}
