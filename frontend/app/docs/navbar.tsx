import {Dropdown} from "~/docs/dropdown"
import {useDropdown} from "~/hooks/useDropdown"

export function Navbar() {
  return(
    <div className="fixed z-9 w-screen h-12 flex justify-end gap-4 px-4 bg-dark-background-primary">
      <ConfigurationItem text="Build tool"/>
      <ConfigurationItem text="Language"/>
      <ConfigurationItem text="Platform"/>
    </div>
  )
}

function ConfigurationItem({text}: { text: string }) {

  const [open, toggleOpen, ref] = useDropdown()

  return (
    <div className="grid grid-cols-2 text-center content-center select-none">
      <div
        className="flex items-center justify-center bg-dark-background-secondary text-sm rounded-l-md px-2 py-1 w-full">
        <span>{text}</span>
      </div>
      <div className="relative flex items-center justify-center bg-[#18A88B] rounded-r-md px-2 py-1 w-full cursor-pointer" ref={ref} onClick={toggleOpen}>
        Gradle
        {
          open && <Dropdown small={false}/>
        }
      </div>
    </div>
  )
}
