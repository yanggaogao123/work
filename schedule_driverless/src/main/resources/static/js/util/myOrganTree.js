/**
 *  combotree 组合树的扩展 */
var myOrganTree = function () {


    /**
     * @Author: zhouyanjie
     * organIdArray: ["9", "10", "12", "11", "14", "15", "13", "2659"]
     * organTreedata: 该用户所属机构
     *利用目标机构数组获取最简便的机构数组
     *         1
     *       /   `\
     *      2     3
     *          /    \
     *         4      5
     * 具体说明
     * 如果传[3,4]->[3]
     * 如果传[3,4,5]->[3]
     * 如果传[4,5]->[3]
     * 如果传[5]->[5]
     * @Description:
     * @Date: Created in 2019/9/18  11:35
     */
     function getTargetOrganIds(organIdArray,organTreedata)
    {
        var targetOrganIdArray =[];
        for(var i=0; i<organIdArray.length;i++){
            recursionOrganTree(organIdArray[i],organTreedata);
        }
        //console.log(organTreedata)
        recursionOrganIdJoinSelected(targetOrganIdArray,organTreedata);
        //console.log(targetOrganIdArray);
        return  targetOrganIdArray;

    }

    //递归树节点,给机构增加seleced属性和selectedOrganChildNum(子机构选择数量)
    function recursionOrganTree(organId,organTreedata) {
        for(var i =0 ; i<organTreedata.length;i++){
            var node =organTreedata[i];
            var childNode =node.children;
            if(node.id ==organId){
                //console.log(node.id+"----");
                //console.log(node.text);
                node.selected = "selected";
                organTreedata.selectedOrganChildNum ++;
                organTreedata[i] = node;
                break;
            }else if(childNode!=null&&childNode.length>0){
                //递归
                if(childNode.selectedOrganChildNum==null){
                    childNode.selectedOrganChildNum =0;
                }
                childNode.myOrganId=node.id; //自己机构的id
                recursionOrganTree(organId,childNode)
            }
        }
    }

    //递归树节点,合并选择机构的Id，如果子机构都勾选则显示父机构id
    function recursionOrganIdJoinSelected(targetOrganIdArray,organTreedata) {
        for(var i =0 ; i<organTreedata.length;i++){
            var node =organTreedata[i];
            var childNode =node.children;
            if(node.selected!=null&&node.selected =="selected"){
                if(organTreedata.selectedOrganChildNum!=null&&organTreedata.selectedOrganChildNum==organTreedata.length){
                    if($.inArray( organTreedata.myOrganId, targetOrganIdArray)==-1){//判断是否已存在于集合中
                        targetOrganIdArray.push(organTreedata.myOrganId);
                    }
                    break;
                }
                //判断是否已存在于集合中
                // 判断父机构是否已存在于集合中
                if($.inArray( node.id, targetOrganIdArray)==-1&&$.inArray( organTreedata.myOrganId, targetOrganIdArray)==-1){
                    targetOrganIdArray.push(node.id);
                }
            }
            if(childNode!=null&&childNode.length>0){
                //递归
                recursionOrganIdJoinSelected(targetOrganIdArray,childNode)
            }
        }
    }

    return {
        getTargetOrganIds: getTargetOrganIds,
    };

}();