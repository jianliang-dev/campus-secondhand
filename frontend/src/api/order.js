import http from './http'

export const createOrder = (productId) =>
  http.post('/order/create', null, { params: { productId } })
export const getBuyerOrders = () => http.get('/order/buyer/list')
export const getSellerOrders = () => http.get('/order/seller/list')
export const mockPay = (orderId) => http.post('/order/mockPay', null, { params: { orderId } })
export const cancelOrder = (orderId) => http.post('/order/cancel', null, { params: { orderId } })
