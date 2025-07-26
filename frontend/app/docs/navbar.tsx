import {Dropdown, DropdownItem} from "~/docs/dropdown"
import {useDropdown} from "~/hooks/useDropdown"
import type {ConfigurationState} from "~/hooks/useConfiguration"
import {motion} from "motion/react"
import {useEffect, useRef, useState} from "react"
import {AnimatePresence} from "motion/react"
import type {DocumentConfiguration} from "~/utils/Configurations"

export function Navbar({buildToolConfigurationState, languageConfigurationState, platformConfigurationState}: {
    buildToolConfigurationState: ConfigurationState,
    languageConfigurationState: ConfigurationState,
    platformConfigurationState: ConfigurationState,
}) {

    return <div className="fixed z-10 top-6 right-6">
        <SettingsContainer
            buildToolConfigurationState={buildToolConfigurationState}
            languageConfigurationState={languageConfigurationState}
            platformConfigurationState={platformConfigurationState}
        />
    </div>
}

function SettingsButton({isOpen, onToggle}: { isOpen: boolean, onToggle: () => void }) {
    return (
        <div
            className="z-50 text-xl absolute right-2 top-2 w-12 h-12 flex justify-center items-center bg-dark-background-secondary rounded-md p-2 transition duration-300 ease-in-out hover:bg-(--project-color)"
            onClick={onToggle}
        >
            {
                isOpen ?
                    <motion.i
                        layoutId="settings-button"
                        initial={false}
                        animate={{rotate: "0deg"}}
                        exit={{rotate: "180deg"}}
                        className="fa-solid fa-xmark"
                    />
                    :
                    <motion.i
                        layoutId="settings-button"
                        initial={false}
                        animate={{rotate: "180deg"}}
                        exit={{rotate: "0deg"}}
                        className="fa-solid fa-gear"
                    />
            }
        </div>
    )
}

function SettingsContainer({buildToolConfigurationState, languageConfigurationState, platformConfigurationState}: {
    buildToolConfigurationState: ConfigurationState,
    languageConfigurationState: ConfigurationState,
    platformConfigurationState: ConfigurationState,
}) {
    const [isOpen, setOpen] = useState(false)
    const containerRef = useRef<HTMLDivElement>(null)

    const ref = useRef<HTMLDivElement>(null)

    const toggleOpen = () => {
        setOpen(!isOpen)
    }

    const handleClickOutside = (event: MouseEvent) => {
        if (ref.current && !ref.current.contains(event.target as Node)) {
            setOpen(false)
        }
    }

    useEffect(() => {
        document.addEventListener("mousedown", handleClickOutside)
        return () => document.removeEventListener("mousedown", handleClickOutside)
    }, [])

    return (
        <div className="w-md h-72" ref={ref}>
            <SettingsButton isOpen={isOpen} onToggle={() => toggleOpen()}/>
            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{
                            y: -120,
                            x: 190,
                            scale: 0.07,
                        }}
                        animate={{
                            y: 0,
                            x: 0,
                            scale: 1,
                        }}
                        exit={{
                            y: -120,
                            x: 190,
                            scale: 0.07,
                        }}
                        className="z-5 w-full h-full bg-dark-background-secondary rounded-lg text-center grid grid-rows-6 py-4 px-6 gap-2"
                        ref={containerRef}
                    >
                        <ElementTitle text="Build Tool"/>
                        <ToggleElement id="build-tool" state={buildToolConfigurationState}/>
                        <ElementTitle text="Language"/>
                        <ToggleElement id="language" state={languageConfigurationState}/>
                        <ElementTitle text="Platform"/>
                        <ToggleElement id="platform" state={platformConfigurationState}/>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    )
}

function ElementTitle({text}: { text: string }) {
    return <div className="flex justify-center items-center">{text}</div>
}

function ToggleElement({id, state}: { id: string, state: ConfigurationState }) {
    const [current, values, setStoredKey] = state

    if (values.length === 0) return <></>

    return <div className="flex gap-1">
        {
            values.map((option, index) => {

                const isFirst = index === 0
                const isLast = index === values.length - 1

                const leftBorder = isFirst ? "rounded-l-lg" : ""
                const rightBorder = isLast ? "rounded-r-lg" : ""

                const isSelected = option.key === current.key

                const pointer = isSelected ? "cursor-default" : "cursor-pointer"

                return <div
                    key={`toggle-element-${index}`}
                    className={`${pointer} ${leftBorder} ${rightBorder} relative flex-1 flex justify-center items-center bg-dark-background-primary`}
                    onClick={() => setStoredKey(option.key)}
                >
                    {isSelected && (
                        <motion.div
                            layoutId={`${id}-selected`}
                            transition={{duration: 0.1}}
                            className={`${leftBorder} ${rightBorder} z-30 absolute w-full h-full bg-(--project-color)`}
                        />
                    )}
                    <span className="relative z-50">{option.name}</span>
                </div>
            })
        }
    </div>
}
