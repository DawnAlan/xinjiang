package com.cj.project.modular.fiducialImport.service;


import java.util.List;
import java.util.Map;

/**
 * 导入考证Service接口
 *
 * @author : lb
 * @date : 2023/10/27 14:42
*/
public interface FiducialImportService {

    List<Map<String, String>> ImportFiducial(String fileName);

    String GetFiducialFieldExcel(String projectCode, String instrumentType);

    String GetFiducialFieldExcel(Map<String, String> fieldMap,String fileName);
}
