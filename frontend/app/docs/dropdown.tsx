export function Dropdown({small}:{small: boolean}) {
  return (
    <div className={`absolute w-36 top-0 ${small ? "mt-7" : "mt-9"} bg-dark-background-secondary rounded-md p-1`}>
      <DropdownItem text="Gradle"/>
      <DropdownItem text="Gradle Kotlin"/>
      <DropdownItem text="Maven"/>
      <DropdownItem text="Maven"/>
      <DropdownItem text="Maven"/>
      <DropdownItem text="Maven"/>
      <DropdownItem text="Maven"/>
    </div>
  )
}

function DropdownItem({text}: { text: string }) {
  return <div className="py-2 hover:bg-dark-background-primary" key={`dropdown-${text}`}>{text}</div>
}