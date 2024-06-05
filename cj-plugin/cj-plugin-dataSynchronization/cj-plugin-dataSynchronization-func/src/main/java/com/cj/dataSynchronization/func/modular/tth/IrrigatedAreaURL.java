package com.cj.dataSynchronization.func.modular.tth;

public interface IrrigatedAreaURL {

    /**
     * 获取token
     */
    public static String GET_TOKEN = "/holdAllow/getToken";

    /**
     * 获取测点列表
     */
    public static String QUERY_MONITOR_BASIC = "/WebApi/api/MonitorInfoApi/QueryMonitorBasic";

    /**
     *获取实时数据
     */
    public static String QUERY_REAL_TIME_DATA = "/WebApi/api/RealTimeDataAPI/QueryRealTimeData";

    public static String GET_ALL_TREE = "/00ZTree_MonitorDept";
    public static String GET_ALL_HISTORY_DATA = "/00RealTimeData_QueryHisData";


    public static String LOAD_PUBLIC_KEY = "/loadPublicKey";

    public final static String LOGIN_VERIFY = "/loginVerify";
    public final static String GET_HISTORY_DATA = "/WebApi/api/BasicApi/Query";
}
