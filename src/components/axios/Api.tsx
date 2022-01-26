import axios from "axios"

const api = axios.create({
  baseURL: "https://api.triumphteam.dev/api",
  withCredentials: true,
})

export default api
