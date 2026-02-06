import http from './http'

export const getWantedList = (params) => http.get('/wanted/list', { params })
export const getWantedDetail = (id) => http.get(`/wanted/${id}`)
export const publishWanted = (data) => http.post('/wanted/publish', data)
export const getMyWanted = () => http.get('/wanted/my')
export const updateWanted = (data) => http.post('/wanted/update', data)
export const deleteWanted = (id) => http.post('/wanted/delete', null, { params: { id } })
export const updateWantedStatus = (id, status) =>
  http.post('/wanted/updateStatus', null, { params: { id, status } })
