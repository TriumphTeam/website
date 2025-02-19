import {Link} from "react-router"
import {useDropdown} from "~/hooks/useDropdown"
import {Dropdown} from "~/docs/dropdown"

export function Sidebar() {
  return (
    <div
      className="fixed z-10 w-screen xl:w-60 2xl:w-72 h-screen hidden xl:flex flex-col gap-4 px-4 justify-center bg-dark-background-primary">
      <ProjectHeader/>
      <ProjectButtons/>
      <SearchBar/>
      <NavigationArea/>
      <SmallFooter/>
    </div>
  )
}

function ProjectHeader() {
  return (
    <div className="grid grid-cols-1 w-full justify-items-center gap-4 pt-6 select-none">
      <div className="flex items-center">
        <img className="self-center h-16"
             src="https://github.com/TriumphTeam/docs/blob/main/triumph-gui/icon.png?raw=true" alt="Logo"/>
      </div>
      <div className="flex items-center gap-2">
        <div className="col-span-1 text-center font-bold text-lg">
          <h1>TRIUMPH GUI</h1>
        </div>
        <VersionComponent/>
      </div>
    </div>
  )
}

function VersionComponent() {
  const [open, toggleOpen, ref] = useDropdown()

  return (
    <div className="relative flex items-center justify-center rounded-sm bg-[#18A88B] px-2 text-center text-md cursor-pointer" ref={ref} onClick={toggleOpen}>
      v3.x.x
      {
        open && <Dropdown small={true}/>
      }
    </div>
  )
}

function ProjectButtons() {
  return (
    <div className="flex justify-center items-center gap-2 px-4">
      <ProjectButton tooltip="Uh" icon="fa-brands fa-discord" link="uh"/>
      <ProjectButton tooltip="Uh" icon="fa-brands fa-discord" link="uh"/>
      <ProjectButton tooltip="Uh" icon="fa-brands fa-discord" link="uh"/>
    </div>
  )
}

function ProjectButton({tooltip, icon, link}: { tooltip: string, icon: string, link?: string }) {
  return (
    <a href={link ? link : ""} target="_blank" rel="noopener noreferrer"
       className="w-1/3 flex justify-center items-center bg-dark-background-secondary rounded-md p-2">
      <i className={icon}/>
    </a>
  )
}

function SearchBar() {
  return (
    <div className="flex items-center w-full mx-auto bg-dark-background-secondary rounded-lg h-12 cursor-pointer">
      <div className="w-full">
        <span
          className="w-full px-4 py-1 rounded-full bg-dark-background-secondary focus:outline-none pointer-events-none text-white/50 select-none">
          Search
        </span>
      </div>
      <div>
        <div className="flex items-center justify-center w-12 h-12 text-white/35 rounded-r-lg">
          <i className="w-5 h-5 fa-solid fa-magnifying-glass"/>
        </div>
      </div>
    </div>
  )
}

function NavigationArea() {
  return (
    <div className="px-4 overflow-y-auto overflow-x-hidden overscroll-contain grow">
      <div className="grid grid-cols-1 gap-10">
        <NavigationGroup text="Example" pages={["example1", "example2", "example3"]}/>
        <NavigationGroup text="Example" pages={["example1", "example2", "example3"]}/>
        <NavigationGroup text="Example" pages={["example1", "example2", "example3"]}/>
        <NavigationGroup text="Example" pages={["example1", "example2", "example3"]}/>
        <NavigationGroup text="Example" pages={["example1", "example2", "example3"]}/>
        <NavigationGroup text="Example" pages={["example1", "example2", "example3"]}/>
        <NavigationGroup text="Example" pages={["example1", "example2", "example3"]}/>
        <NavigationGroup text="Example" pages={["example1", "example2", "example3"]}/>
      </div>
    </div>
  )
}

function NavigationGroup({text, pages}: { text: string, pages: string[] }) {
  return (
    <div>
      <h1 className="text-white xl:text-lg 2xl:text-xl font-bold">{text}</h1>
      {
        pages.map((page, _) => NavigationPage(page, page, false))
      }
    </div>
  )
}

function NavigationPage(text: string, link: string, selected: boolean) {
  return (
    <div className="pt-2 text-white/70">
      <Link to={link} className="xl:text-base 2xl:text-lg transition ease-in-out delay-100 project-color-hover">
        {text}
      </Link>
    </div>
  )
}

function SmallFooter() {

  const year = new Date().getFullYear()

  return (
    <div className="flex-none text-[0.5em] text-center py-2">
      Copyright © 2020-{year}, TriumphTeam. All Rights Reserved.
    </div>
  )
}