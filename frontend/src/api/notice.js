import http from './http'

export const getNotices = (limit = 5) => http.get('/notice/latest', { params: { limit } })
export const getNoticeList = () => http.get('/notice/list')
