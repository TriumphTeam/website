import axios from "axios"

const api = axios.create({
  baseURL: "https://api.triumphteam.dev/",
  withCredentials: true,
})

export default api
