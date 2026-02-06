import http from './http'

export const getAddresses = () => http.get('/address/list')
export const addAddress = (data) => http.post('/address/add', data)
export const updateAddress = (data) => http.post('/address/update', data)
export const deleteAddress = (id) => http.post('/address/delete', null, { params: { id } })
export const setDefaultAddress = (id) =>
  http.post('/address/setDefault', null, { params: { id } })
