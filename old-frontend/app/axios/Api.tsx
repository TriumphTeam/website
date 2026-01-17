import axios from "axios"

export const API_URL = "http://localhost:8001/"
export const ASSETS_URL = "http://localhost:8001/assets"

const api = axios.create({
    baseURL: API_URL,// "https://api.triumphteam.dev/",
    withCredentials: true,
})

export default api
