import http from './http'

export const getCategories = () => http.get('/category/list')
export const getCircleCategories = () => http.get('/circleCategory/list')
