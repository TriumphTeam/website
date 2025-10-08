import {type RouteConfig, index, route} from "@react-router/dev/routes"

export default [
  index("home/home.tsx"),
  route("docs/:versionProject?/:projectPage?/:page?", "docs/docs.tsx"),
  route("404", "404/404.tsx"),
] satisfies RouteConfig;
