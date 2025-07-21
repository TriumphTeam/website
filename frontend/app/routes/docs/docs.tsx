"use client"

import type {Route} from "../+types/home"
import {redirect, useLoaderData} from "react-router"
import {Sidebar} from "~/docs/sidebar"
import {Navbar} from "~/docs/navbar"
import {Content} from "~/docs/content"
import api from "~/axios/Api"
import {type ProjectVersion} from "@lichthund/triumph-docs-serializable"
import React from "react"
import {BuildTools, Languages, Platforms, type StorageKeys} from "~/utils/Configurations"
import {useConfiguration} from "~/hooks/useConfiguration"
import {SWRConfig} from "swr"

type PageUrl = {
    version: string | null,
    project: string,
    page: string,
}

// provides `loaderData` to the component
export async function clientLoader({params}: Route.ClientLoaderArgs): Promise<ProjectVersion | undefined> {

    const requestUrl = createRequestUrl(params.versionProject, params.projectPage, params.page)
    if (!requestUrl) return undefined

    return api.get<ProjectVersion>(
        "/project",
        {
            params: {
                version: requestUrl.version,
                project: requestUrl.project,
            },
        },
    )
        .then(r => r.data)
        .catch(e => undefined)
}

export default function Docs() {
    const data = useLoaderData<typeof clientLoader>() as ProjectVersion | undefined

    if (!data) return redirect("/404")

    const storageKeys: StorageKeys = {
        buildTool: `${data.project}-build-tool`,
        language: `${data.project}-language`,
        platform: `${data.project}-platform`,
    }

    return <div key="root" style={{"--project-color": data.document.color} as React.CSSProperties}>
        <Sidebar key="side-bar" name={data.name} document={data.document}/>
        <SWRConfig value={{
            dedupingInterval: 15000,
            fetcher: (url: string) => api.get(url).then(r => r.data),
            onErrorRetry: (error) => {
                if (error.status === 404) return
            },
        }}>
            <MainContent key="main-content" storageKeys={storageKeys} data={data}/>
        </SWRConfig>
    </div>
}

function MainContent({storageKeys, data}: { storageKeys: StorageKeys, data: ProjectVersion }) {

    const document = data.document

    const buildToolsState = useConfiguration(storageKeys.buildTool, document.buildTools.map(value => BuildTools[value]))
    const languagesState = useConfiguration(storageKeys.language, document.languages.map(value => Languages[value]))
    const platformsState = useConfiguration(storageKeys.platform, document.platforms.map(value => Platforms[value]))

    return <>
        <Navbar
            key="nav-bar"
            buildToolConfigurationState={buildToolsState}
            languageConfigurationState={languagesState}
            platformConfigurationState={platformsState}
        />
        <Content
            key="content"
            version={data.version}
            buildToolConfigurationState={buildToolsState}
            languageConfigurationState={languagesState}
            platformConfigurationState={platformsState}
        />
    </>
}

function createRequestUrl(version: string | undefined, project: string | undefined, page: string | undefined): PageUrl | undefined {
    if (version && project && page) return {version: version, project: project, page: page}
    if (version && project && !page) return {version: null, project: version, page: project}

    return undefined
}