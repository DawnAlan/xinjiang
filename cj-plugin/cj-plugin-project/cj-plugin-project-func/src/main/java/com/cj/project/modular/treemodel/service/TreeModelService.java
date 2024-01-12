package com.cj.project.modular.treemodel.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.project.api.treemodel.dto.TreeModelDto;
import com.cj.project.api.treemodel.dto.TreeModelTreeDto;
import com.cj.project.api.treemodel.dto.TreePointNodeAddDto;
import com.cj.project.api.treemodel.entity.TreeModel;

import java.util.List;

/**
 * 测点树Service接口
 *
 * @author Lb
 * @date  2023/09/14 16:41
 **/
public interface TreeModelService extends IService<TreeModel> {

    /**
     * 生成测点树
     *
     * @author Lb
     * @date  2023/09/14 16:41
     */
    List<Tree<String>> tree(TreeModelTreeDto treeModelTreeParam);

    /**
     * 添加测点树节点
     *
     * @author Lb
     * @date  2023/09/14 16:41
     */
    void add(TreeModelDto treeModelAddParam);

    /**
     * 绑定测点到测点树节点
     * @param pointNodeAddParam
     */
    void addPointNode(TreePointNodeAddDto pointNodeAddParam);

    /**
     * 编辑测点树
     *
     * @author Lb
     * @date  2023/09/14 16:41
     */
    void edit(TreeModelDto treeModelEditParam);

    /**
     * 删除测点树节点
     *
     * @author Lb
     * @date  2023/09/14 16:41
     */
    void delete(List<TreeModelTreeDto> treeModelIdParamList);

    /**
     * 删除绑定的节点
     * @param pointIdList 测点ID列表
     * @param category 测点树类型|不传默认全部
     */
    void deletePointNode(List<String> pointIdList, String category);

    /**
     * 获取测点树详情
     *
     * @author Lb
     * @date  2023/09/14 16:41
     */
    TreeModel detail(TreeModelTreeDto treeModelIdParam);

    /**
     * 获取测点树详情
     *
     * @author Lb
     * @date  2023/09/14 16:41
     **/
    TreeModel queryEntity(String id);

    /**
     * 初始化测点考证书
     *
     * @param projectCode 项目编号
     */
    void generateTree(String projectCode);

    /// <summary>
    /// 获取所有未绑定到测点树的测点
    /// </summary>
    /// <param name="Project_Code">项目ID</param>
    /// <param name="Instrument_Name">仪器名称</param>
    /// <param name="TreeType">树结构类型 1：分部分项结果树，2：仪器类型树；默认为1</param>
    /// <returns></returns>
    //     [HttpGet, Route("categories/getnotree")]
    // public IHttpActionResult GetNoTreeNode(string Project_Code, string Instrument_Name, int TreeType = 1)

    //
    /// <summary>
    /// 树结构只到仪器类型级
    /// </summary>
    /// <param name="Project_Code">项目ID，可传可不传，不传则获取所有项目</param>
    /// <returns></returns>
    //     [HttpGet, Route("categories/newtreenopoint")]
    // public IHttpActionResult NewModleTreeNoPoint(string Project_Code = null, int TreeType = 1)

    /// <summary>
    /// 树结构接口合并
    /// </summary>
    /// <param name="TeamID">用户TeamID,不传为获取所有树节点，传为获取权限树</param>
    /// <param name="NodeType">是否为懒加载树结构，传0为懒加载，传1为全部树结构。可不传，默认为 0</param>
    /// <param name="Project_Code">项目ID，可不传默认为001三峡项目</param>
    /// <param name="TreeType">树结构类型，可不传默认为1，分部分项结构树</param>
    /// <returns></returns>
    //     [HttpGet, Route("categories/getmergetree")]
    // public IHttpActionResult GetMergeTree(string TeamID = "", string NodeType = "0", string Project_Code = "001", int TreeType = 1, string Monitor_Name = "")

    /// <summary>
    /// 模糊查询树目录
    /// </summary>
    /// <param name="NodeName">模糊查询字段</param>
    /// <param name="TeamID">用户TeamID,不传为获取所有树节点，传为获取权限树</param>
    /// <param name="Project_Code">项目ID,默认为001</param>
    /// <param name="TreeType">树结构类型，默认为1</param>
    /// <returns></returns>
    //     [HttpGet, Route("categories/getvaguetree")]
    // public IHttpActionResult GetVagueTree(string NodeName,string TeamID = "", string Project_Code = "001", int TreeType = 1)

    /// <summary>
    /// 单独获取自动化测点树
    /// </summary>
    /// <param name="Project_Code">项目Code</param>
    /// <param name="TreeType">树结构类型,1位分部分项结构树,2为仪器类型结构树</param>
    /// <returns></returns>
    //     [HttpGet, Route("categories/getautopointtree")]
    // public IHttpActionResult GetAutoPointTree(string Project_Code = "001", int TreeType = 1)

    /// <summary>
    /// 根据父级获取子集
    /// </summary>
    /// <param name="NodeID">父级的NodeID</param>
    /// <param name="TeamID">登录人的TeamID，不传为正常结构下的数据，传TeamID则是权限树结构的获取所有子集。</param>
    /// <param name="Project_Code">项目不传，默认为001</param>
    /// <returns></returns>
    //     [HttpGet, Route("categories/newtreeparent")]
    // public IHttpActionResult NewModleTreeParent(string NodeID, string TeamID = null, string Project_Code = "001")

    /// <summary>
    /// 测点树模糊查询
    /// </summary>
    /// <param name="Project_Code">项目ID</param>
    /// <param name="NodeName">需要查询的关键字</param>
    /// <param name="TreeType">树类型：1为分部分项结构树，2为仪器类型树，可不传 默认为1</param>
    /// <param name="TeamId">权限ID</param>
    /// <returns></returns>
    //     [HttpGet, Route("categories/fuzzyqueryfortree")]
    // public IHttpActionResult FuzzyQueryForTree(string Project_Code, string NodeName, int TreeType = 1, string TeamId = "")



}
