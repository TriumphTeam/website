"use client"
import {motion} from "motion/react"
import React, {useState} from "react"
import type {VersionData} from "@lichthund/triumph-docs-serializable"
import useSWR, {SWRConfig} from "swr"
import api, {ASSETS_URL} from "~/axios/Api"
import {Link} from "react-router"

type Project = {
    id: string,
    name: string,
    color: string,
    versions: VersionData[],
}

export default function Home() {
    return <div
        className="w-screen min-h-screen bg-[radial-gradient(#202023_1px,transparent_1px)] [background-size:16px_16px]"
    >
        <div className="grid grid-rows-1 w-full h-screen justify-items-center content-center">
            <div className="row-span-1 self-center flex flex-col gap-24">
                <div className="flex flex-col gap-2">
                    <img className="self-center h-32" src="/assets/logo.png" alt="Logo"/>
                    <h1 className="text-white text-center text-6xl font-bold uppercase [text-shadow:_0px_0px_65px_#8d4eb8]">Triumph
                        Team</h1>
                    <h2 className="text-white/50 text-center">Making libraries for your block game projects.</h2>
                </div>
                <Test/>
                <SWRConfig value={{
                    dedupingInterval: 15000,
                    fetcher: (url: string) => api.get(url).then(r => r.data),
                    shouldRetryOnError: false,
                }}>
                    <Projects/>
                </SWRConfig>
            </div>
        </div>
    </div>
}

function Test() {
    const [open, setOpen] = useState(false)

    return <div className="fixed z-[100] top-0 text-2xl pl-6 py-8">
        <motion.div
            animate={{
                rotate: open ? "180deg" : "0deg",
                scale: open ? 1 : 0.5,
            }}
            onClick={() => setOpen(!open)}
        >
            {open ? <i className="fa-solid fa-xmark"/> : <i className="fa-solid fa-bars"/>}
        </motion.div>
    </div>
}

function Projects() {
    const {data, error} = useSWR<Project[]>(`/projects`)

    if (error || !data) return <div className="text-white/50 text-center">Failed to load projects</div>

    return <div className="grid grid-cols-1 md:grid-cols-3 gap-6 self-center">
        {
            data.map((project, _) => <ProjectCard key={`project-${project.name}`} project={project}/>)
        }
    </div>
}

function ProjectCard({project}: { project: Project }) {

    function Version({projectId, version}: { projectId: string, version: VersionData }) {
        const color = version.current ? "bg-(--project-color)" : "bg-dark-background-secondary"
        return <Link to={`/docs/${version.reference}/${projectId}/introduction`} relative="path">
            <motion.div
                whileHover={{scale: 1.1}}
                className={`${color} px-3 py-1 rounded-sm`}
            >
                {version.reference}
            </motion.div>
        </Link>
    }

    return <div
        style={{"--project-color": project.color} as React.CSSProperties}
        className="flex flex-col p-8 gap-3 justify-center items-center
     bg-dark-background-primary noise border-2 border-(--project-color)/10 rounded-lg"
    >
        <div>
            <img className="h-16 justify-self-center"
                 src={`${ASSETS_URL}/${project.id}/icon.png`} alt="Logo"/>
        </div>
        <div className="text-white text-xl uppercase">{project.name}</div>
        <div className="flex flex-row gap-3">
            {
                project.versions.map((version, _) => <Version key={`version-${version}`} projectId={project.id}
                                                              version={version}/>)
            }
        </div>
    </div>
}
