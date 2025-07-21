import {Link, redirect, useParams} from "react-router"
import {useDropdown} from "~/hooks/useDropdown"
import {Dropdown, DropdownItem} from "~/docs/dropdown"
import {
    type VersionDocument,
    type NavigationPage,
    type VersionData,
    type Nullable,
} from "@lichthund/triumph-docs-serializable"

export function Sidebar({name, document}: { name: string, document: VersionDocument }) {

    return (
        <div
            className="fixed z-10 w-screen xl:w-60 2xl:w-72 h-screen hidden xl:flex flex-col gap-4 px-4 justify-center bg-dark-background-primary">
            <ProjectHeader
                key="project-header"
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
            <NavigationArea key="navigation-area" document={document}/>
            <SmallFooter key="small-footer"/>
        </div>
    )
}

function ProjectHeader({projectName, versions}: { projectName: string, versions: Array<VersionData> }) {
    return (
        <div className="grid grid-cols-1 w-full justify-items-center gap-4 pt-6 select-none">
            <div className="flex items-center">
                <img className="self-center h-16"
                     src="https://github.com/TriumphTeam/docs/blob/main/triumph-gui/icon.png?raw=true" alt="Logo"/>
            </div>
            <div className="flex items-center gap-2">
                <div className="col-span-1 text-center font-bold text-lg uppercase">
                    <h1>{projectName}</h1>
                </div>
                <VersionComponent key="version-component" versions={versions}/>
            </div>
        </div>
    )
}

function VersionComponent({versions}: { versions: Array<VersionData> }) {
    const [open, toggleOpen, ref] = useDropdown()

    const isSingular = versions.length <= 1
    const cursor = isSingular ? "default" : "pointer"
    const current = versions.find(version => version.current)

    if (!current) return <></>

    return (
        <div
            className={`relative flex items-center justify-center rounded-sm bg-(--project-color) px-2 text-center text-md cursor-${cursor}`}
            ref={ref} onClick={toggleOpen}>
            {current.reference}
            {
                (open && versions.length > 1) && (
                    <Dropdown key="version-dropdown" small={true}>
                        {
                            versions.map((version) => <DropdownItem
                                key={`version-dropdown-${version}`}
                                text={version.reference}
                                onClick={() => {
                                    console.log("click click version")
                                }}
                            />)
                        }
                    </Dropdown>
                )
            }
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
    return (
        <div className="flex items-center w-full mx-auto bg-dark-background-secondary rounded-lg h-12 cursor-pointer">
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
    )
}

function NavigationArea({document}: { document: VersionDocument }) {
    return (
        <div className="px-4 overflow-y-auto overflow-x-hidden overscroll-contain grow">
            <div className="grid grid-cols-1 gap-10">
                {
                    document.groups.map((group) => <NavigationGroupArea
                        key={`navigation-group-${group}`} text="Example"
                        pages={group.pages}
                    />)
                }
            </div>
        </div>
    )
}

function NavigationGroupArea({text, pages}: { text: string, pages: NavigationPage[] }) {
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

function NavigationPageArea({text, link}: { text: string, link: string }) {
    const {page} = useParams()

    const color = page === link ? "text-(--project-color)" : ""

    return (
        <div className={`pt-2 ${color}`}>
            <Link to={`../${link}`} relative="path"
                  className="xl:text-base 2xl:text-lg transition ease-in-out delay-100 project-color-hover">
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