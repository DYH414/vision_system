import request from '@/utils/request'

// 查询视力监测记录列表
export function listVisonrecord(query) {
  return request({
    url: '/visionrecord/visonrecord/list',
    method: 'get',
    params: query
  })
}

// 查询视力监测记录详细
export function getVisonrecord(id) {
  return request({
    url: '/visionrecord/visonrecord/' + id,
    method: 'get'
  })
}

// 新增视力监测记录
export function addVisonrecord(data) {
  return request({
    url: '/visionrecord/visonrecord',
    method: 'post',
    data: data
  })
}

// 修改视力监测记录
export function updateVisonrecord(data) {
  return request({
    url: '/visionrecord/visonrecord',
    method: 'put',
    data: data
  })
}

// 删除视力监测记录
export function delVisonrecord(id) {
  return request({
    url: '/visionrecord/visonrecord/' + id,
    method: 'delete'
  })
}
