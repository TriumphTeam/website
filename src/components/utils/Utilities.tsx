export const openLink = (link: string) => {
  window.open(link)
}

export const SideBarSize = "300px"

export interface Project {
  name: string,
  version: string,
}

export interface Projects {
  plugin: Project[],
  library: Project[]
}
