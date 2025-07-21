import axios from "axios"

const api = axios.create({
    baseURL: "http://localhost:8001/api",// "https://api.triumphteam.dev/",
    withCredentials: true,
})

export default api