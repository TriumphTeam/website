"use client"
import {Link, NavLink, useParams} from "react-router"
import {useOpenable} from "~/hooks/useOpenable"
import {Dropdown, DropdownItem} from "~/docs/dropdown"
import {
    ContentSection,
    type NavigationPage,
    type Nullable, type PageDocument,
    type VersionData,
    type VersionDocument,
} from "@lichthund/triumph-docs-serializable"
import React, {useState} from "react"
import {AnimatePresence, motion} from "motion/react"
import useSWR from "swr"

export function Sidebar({project, name, document}: { project: string, name: string, document: VersionDocument }) {
    const [open, setOpen] = useState(false)

    function ControlButton() {
        return <div className="xl:hidden fixed z-[100] top-0 text-2xl pl-6 py-8">
            <motion.div
                initial={{rotate: "0deg", scale: 0.5}}
                animate={{rotate: "180deg", scale: 1}}
                onClick={() => setOpen(!open)}
            >
                {open ? <i className="fa-solid fa-xmark"/> : <i className="fa-solid fa-bars"/>}
            </motion.div>
        </div>
    }

    return <>
        <ControlButton/>
        <div className="xl:hidden w-6"/>
        <motion.div
            initial={{x: "-100%"}}
            animate={{x: open ? "0%" : "-100%"}}
            transition={{
                type: "spring",
                stiffness: 500,
                damping: 25,
                duration: 0.1,
            }}
            className={`
                fixed w-screen z-50 h-screen flex
                md:w-72
                xl:sticky xl:top-0 xl:min-w-60 xl:w-60 xl:flex xl:!transform-none
                2xl:min-w-72 2xl:w-72
                flex-col gap-4 px-4 justify-center
                bg-dark-background-primary noise
                border-r-2 border-dark-background-secondary
            `}>
            <ProjectHeader
                key="project-header"
                project={project}
                projectName={name}
                versions={document.versions}
            />
            <ProjectButtons
                key="project-buttons"
                discord={document.discord}
                github={document.github}
                javadocs={document.javadocs}
            />
            <SearchBar key="search-bar"/>
            <NavigationArea
                key="navigation-area"
                document={document}
            />
            <SmallFooter key="small-footer"/>
        </motion.div>
    </>
}

function ProjectHeader({project, projectName, versions}: {
    project: string,
    projectName: string,
    versions: Array<VersionData>
}) {
    return (
        <div className="grid grid-cols-1 w-full justify-items-center gap-4 pt-6 select-none">
            <div className="flex items-center">
                <Link to="/" relative="path">
                    <img className="self-center h-16"
                         src={`http://localhost:8001/assets/${project}/icon.png`} alt="Logo"/>
                </Link>
            </div>
            <div className="flex items-center gap-2">
                <div className="col-span-1 text-center font-bold text-lg uppercase">
                    <h1>{projectName}</h1>
                </div>
                <VersionComponent key="version-component" project={project} versions={versions}/>
            </div>
        </div>
    )
}

function VersionComponent({project, versions}: { project: string, versions: Array<VersionData> }) {
    const [open, toggleOpen, ref] = useOpenable()

    const isSingular = versions.length <= 1
    const cursor = isSingular ? "default" : "pointer"
    const current = versions.find(version => version.current)

    if (!current) return <></>

    return (
        <div
            className={`relative flex items-center justify-center rounded-sm bg-(--project-color) px-2 text-center text-md cursor-${cursor}`}
            ref={ref} onClick={toggleOpen}>
            {current.reference}
            <AnimatePresence initial={false}>
                {
                    (open && versions.length > 1) && (
                        <Dropdown key="version-dropdown" small={true}>
                            {
                                versions.map((version) => <DropdownItem
                                    key={`version-dropdown-${version}`}
                                    text={version.reference}
                                    destination={`docs/${version.reference}/${project}/introduction`}
                                />)
                            }
                        </Dropdown>
                    )
                }
            </AnimatePresence>
        </div>
    )
}

function ProjectButtons({discord, github, javadocs}: {
    github: Nullable<string>,
    javadocs: Nullable<string>,
    discord: Nullable<string>,
}) {
    return (
        <div className="flex justify-center items-center gap-2 px-4">
            <ProjectButton key="project-button-discord" tooltip="Discord" icon="fa-brands fa-discord" link={discord}/>
            <ProjectButton key="project-button-github" tooltip="Github" icon="fa-brands fa-github" link={github}/>
            <ProjectButton key="project-button-javadocs" tooltip="Javadocs" icon="fa-solid fa-book" link={javadocs}/>
        </div>
    )
}

function ProjectButton({tooltip, icon, link}: { tooltip: string, icon: string, link: Nullable<string> }) {

    const isEnabled = link != null

    const className = `w-1/3 flex justify-center items-center bg-dark-background-secondary rounded-md p-2`

    if (!isEnabled) return (
        <div className={`${className} text-white/10`}>
            <i className={icon}/>
        </div>
    )

    return (
        <a
            href={link ? link : ""}
            target="_blank"
            rel="noopener noreferrer"
            data-tooltip={tooltip}
            className={`${className} transition duration-300 ease-in-out hover:bg-(--project-color)`}
        >
            <i className={icon}/>
        </a>
    )
}

function SearchBar() {

    const [open, toggleOpen, ref] = useOpenable()

    return <>
        <div
            className="flex items-center w-full mx-auto bg-dark-background-secondary rounded-lg h-12 cursor-pointer"
            onClick={toggleOpen}
        >
            <div className="w-full">
        <span
            className="w-full px-4 py-1 rounded-full focus:outline-none pointer-events-none text-white/50 select-none">
          Search
        </span>
            </div>
            <div>
                <div className="flex items-center justify-center w-12 h-12 text-white/35 rounded-r-lg">
                    <i className="w-5 h-5 fa-solid fa-magnifying-glass"/>
                </div>
            </div>
        </div>
        <AnimatePresence initial={false}>
            {
                open && <SearchArea reference={ref}/>
            }
        </AnimatePresence>
    </>
}

function SearchArea({reference}: { reference: React.RefObject<HTMLDivElement | null> }) {
    return <motion.div
        initial={{opacity: 0}}
        animate={{opacity: 1}}
        exit={{opacity: 0}}
        className="fixed w-screen h-screen top-0 left-0 bg-black/60 backdrop-blur-sm z-100 flex justify-center items-center"
    >
        <motion.div
            initial={{y: "100%"}}
            animate={{y: "0%"}}
            transition={{
                type: "spring",
                stiffness: 500,
                damping: 25,
            }}
            ref={reference}
            className="w-32 h-32 bg-blue-500"
        >
            <SearchDataArea/>
        </motion.div>
    </motion.div>
}

function SearchDataArea() {
    const {data, error} = useSWR<ContentSection[]>(`/search-data?version=3`)

    if (error || !data) {
        console.log(error)
        console.log(data)
        return <></>
    }

    console.log(data)

    return <>TITS</>
}

function NavigationArea({document}: { document: VersionDocument }) {
    return (
        <div className="px-4 overflow-y-auto overflow-x-hidden overscroll-contain grow">
            <div className="grid grid-cols-1 gap-10">
                {
                    document.groups.map((group) => <NavigationGroupArea
                        key={`navigation-group-${group}`}
                        text={group.name}
                        pages={group.pages}
                    />)
                }
            </div>
        </div>
    )
}

function NavigationGroupArea({text, pages}: { text: string, pages: NavigationPage[], }) {
    return (
        <div>
            <h1 className="text-white xl:text-lg 2xl:text-xl font-bold">{text}</h1>
            {
                pages.map((page, _) => <NavigationPageArea
                    key={`navigation-page-${page.id}`}
                    text={page.name}
                    link={page.id}
                />)
            }
        </div>
    )
}

function NavigationPageArea({text, link}: { text: string, link: string, }) {
    const {page} = useParams()

    const color = page === link ? "text-(--project-color)" : ""

    return (
        <div className={`pt-2 ${color}`}>
            <NavLink
                to={`../${link}`} relative="path"
                className="xl:text-base 2xl:text-lg hover:text-(--project-color) transition ease-in-out"
            >
                {text}
            </NavLink>
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
