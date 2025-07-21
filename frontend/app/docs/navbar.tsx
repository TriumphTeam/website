import {Dropdown, DropdownItem} from "~/docs/dropdown"
import {useDropdown} from "~/hooks/useDropdown"
import type {ConfigurationState} from "~/hooks/useConfiguration"

export function Navbar({buildToolConfigurationState, languageConfigurationState, platformConfigurationState}: {
    buildToolConfigurationState: ConfigurationState,
    languageConfigurationState: ConfigurationState,
    platformConfigurationState: ConfigurationState,
}) {

    return (
        <div className="fixed z-9 w-screen h-12 flex justify-end gap-4 px-4 bg-dark-background-primary  box-border">
            <ConfigurationItem key="configuration-item-build-tool" text="Build tool"
                               state={buildToolConfigurationState}/>
            <ConfigurationItem key="configuration-item-language" text="Language" state={languageConfigurationState}/>
            <ConfigurationItem key="configuration-item-platform" text="Platform" state={platformConfigurationState}/>
        </div>
    )
}

function ConfigurationItem({text, state}: {
    text: string,
    state: ConfigurationState,
}) {

    const [open, toggleOpen, ref] = useDropdown()
    const [current, values, setStoredKey] = state

    if (values.length === 0) return <></>

    const isSingular = values.length <= 1

    const background = isSingular ? "bg-disabled" : "bg-(--project-color)"
    const pointer = isSingular ? "pointer-events-none" : "cursor-pointer"

    return (
        <div className="grid grid-cols-2 text-center content-center select-none">
            <div
                className="flex items-center justify-center bg-dark-background-secondary text-sm rounded-l-md px-2 py-1 w-full">
                <span>{text}</span>
            </div>
            <div
                className={`relative flex items-center justify-center ${background} rounded-r-md px-2 py-1 w-full ${pointer}`}
                ref={ref} onClick={toggleOpen}>
                {current.name}
                {
                    open && (
                        <Dropdown key={`dropdown-menu-${current.key}`} small={false}>
                            {
                                values.map(value => <DropdownItem
                                    key={`dropdown-item-${value.key}`}
                                    text={value.name}
                                    onClick={() => {
                                        setStoredKey(value.key)
                                    }}
                                />)
                            }
                        </Dropdown>
                    )
                }
            </div>
        </div>
    )
}
