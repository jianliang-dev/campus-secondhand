import http from './http'

export const getCarouselList = () => http.get('/carousel/list')
