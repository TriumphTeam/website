export const openLink = (link: string) => {
  window.open(link)
}

export const SideBarSize = "300px"

export interface Project {
  id: string,
  name: string,
  icon: string,
  version: string,
  color: string[],
}

export interface Projects {
  plugin: Project[],
  library: Project[],
}
