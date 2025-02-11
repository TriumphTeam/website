import {type RouteConfig, index, route} from "@react-router/dev/routes"

export default [
  index("routes/home.tsx"),
  route("docs/:versionProject?/:projectPage?/:page?", "routes/docs/docs.tsx"),
  route("404", "routes/404.tsx"),
] satisfies RouteConfig;
