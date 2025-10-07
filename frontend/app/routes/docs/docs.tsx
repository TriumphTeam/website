"use client"

import type {Route} from "../+types/home"
import {redirect, useLoaderData, useNavigation} from "react-router"
import {Sidebar} from "~/docs/sidebar"
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

    console.log("Loading", requestUrl)

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

export function shouldRevalidate({currentParams, nextParams}: { currentParams: any, nextParams: any }) {
    const current = createRequestUrl(currentParams.versionProject, currentParams.projectPage, currentParams.page)
    const next = createRequestUrl(nextParams.versionProject, nextParams.projectPage, nextParams.page)

    if (!current || !next) return true

    return current.project !== next.project || current.version !== next.version
}

export default function Docs() {
    const data = useLoaderData<typeof clientLoader>() as ProjectVersion | undefined

    if (!data) return redirect("/404")

    const storageKeys: StorageKeys = {
        buildTool: `${data.project}-build-tool`,
        language: `${data.project}-language`,
        platform: `${data.project}-platform`,
    }

    return <div
        style={{"--project-color": data.document.color} as React.CSSProperties}
        className="bg-[radial-gradient(#202023_1px,transparent_1px)] [background-size:16px_16px]"
    >
        <div className="flex gap-8">
            <Sidebar key="side-bar" name={data.name} document={data.document}/>
            <SWRConfig value={{
                dedupingInterval: 15000,
                fetcher: (url: string) => api.get(url).then(r => r.data),
                onErrorRetry: (error) => {
                    if (error.status === 404) redirect("/404")
                },
            }}>
                <MainContent key="main-content" storageKeys={storageKeys} data={data}/>
            </SWRConfig>
        </div>
    </div>
}

function MainContent({storageKeys, data}: { storageKeys: StorageKeys, data: ProjectVersion }) {

    const document = data.document

    const buildToolsState = useConfiguration(storageKeys.buildTool, document.buildTools.map(value => BuildTools[value]))
    const languagesState = useConfiguration(storageKeys.language, document.languages.map(value => Languages[value]))
    const platformsState = useConfiguration(storageKeys.platform, document.platforms.map(value => Platforms[value]))

    /*
    <Navbar
            key="nav-bar"
            buildToolConfigurationState={buildToolsState}
            languageConfigurationState={languagesState}
            platformConfigurationState={platformsState}
        />
     */

    return <Content
        key="content"
        version={data.version}
        buildToolState={buildToolsState}
        languageState={languagesState}
        platformState={platformsState}
    />
}

function createRequestUrl(version: string | undefined, project: string | undefined, page: string | undefined): PageUrl | undefined {
    if (version && project && page) return {version: version, project: project, page: page}
    if (version && project && !page) return {version: null, project: version, page: project}

    return undefined
}