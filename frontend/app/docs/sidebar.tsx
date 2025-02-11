import {Link} from "react-router"
import {useState} from "react"

export function Sidebar() {
  return (
    <div
      className="fixed z-10 w-screen xl:w-60 2xl:w-72 h-screen hidden xl:flex flex-col gap-4 px-4 justify-center">
      <ProjectHeader/>
      <ConfigurationArea/>
      <SearchBar/>
      <NavigationArea/>
    </div>
  )
}

function ProjectHeader() {
  return (
    <div className="grid grid-cols-1 gap-1 w-full justify-items-center">
      <div className="flex items-center h-12 pt-4 gap-2">
        <Link className="flex items-center" to="/">
          <img className="self-center h-6"
               src="https://github.com/TriumphTeam/docs/blob/main/triumph-gui/icon.png?raw=true" alt="Logo"/>
        </Link>
        <div className="col-span-1 text-center font-bold text-lg">
          <h1>TRIUMPH GUI</h1>
        </div>
        <div className="inline-flex flex-shrink-0 items-center rounded-sm bg-[#18A88B] px-2 text-center text-md">
          v3.x.x
        </div>
      </div>
    </div>
  )
}

function ConfigurationArea() {

  const [selected, setSelected] = useState("Example")

  return (
    <div className="grid grid-cols-1 gap-2 w-full justify-items-center">
      <h2>Build tool</h2>
      <div className="flex items-center justify-center">
        <div className="flex items-center bg-dark-background-secondary rounded-sm p-1">
          <ConfigurationItem text="Gradle" selected/>
          <ConfigurationItem text="Maven"/>
        </div>
      </div>
      <h2>Language</h2>
      <div className="flex items-center justify-center">
        <div className="flex items-center bg-dark-background-secondary rounded-sm p-1">
          <ConfigurationItem text="Java" selected/>
          <ConfigurationItem text="Kotlin"/>
        </div>
      </div>
      <h2>Platform</h2>
      <div className="flex items-center justify-center">
        <div className="flex items-center bg-dark-background-secondary rounded-sm p-1">
          <ConfigurationItem text="Paper" selected/>
          <ConfigurationItem text="Fabric"/>
        </div>
      </div>
    </div>
  )
}

function ConfigurationItem({text, selected}: { text: string, selected?: boolean }) {
  return <div
    className={selected ? "border border-[#18A88B] rounded-md p-2" : "border border-dark-background-secondary hover:border-[#18A88B] p-2 rounded-sm"}>{text}</div>
}

function SearchBar() {
  return (
    <div className="flex items-center w-full mx-auto bg-search-bg rounded-lg h-12">
      <div className="w-full">
        <span
          className="w-full px-4 py-1 rounded-full bg-search-bg focus:outline-none pointer-events-none text-white/50 select-none">
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