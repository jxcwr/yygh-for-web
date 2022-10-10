import request from '@/utils/request'



const API="/admin/hosp/hospital";

export default {
  getHospitalList(pageNum,pageSize,searchObj) {
    return request({
      url: `${API}/page/${pageNum}/${pageSize}`,
      method: 'get',
      params:searchObj,  //普通参数
     /* data:{            //json数据

      }*/
    })
  },
  //admin/cmn/dict/findChildList/{pid}
  getChildList(pid) {
    return request({
      url: `/admin/cmn/dict/findChildList/${pid}`,
      method: 'get'
    })
  },

  updateStatus(id,status) {
    return request({
      url: `${API}/update/${id}/${status}`,
      method: 'get'
    })
  },

  getHospById(id){
    return request({
      url: `${API}/show/${id}`,
      method: 'get'
    })
  }
}

