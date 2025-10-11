"use client"
import type {Route} from "../../.react-router/types/app/routes/+types"
import {redirect, useLoaderData, useNavigate} from "react-router"
import {Sidebar} from "~/docs/sidebar"
import {Content} from "~/docs/content"
import api from "~/axios/Api"
import {type ProjectVersion} from "@lichthund/triumph-docs-serializable"
import React from "react"
import {BuildTools, Languages, Platforms, type StorageKeys} from "~/utils/Configurations"
import {useConfiguration} from "~/hooks/useConfiguration"
import {SWRConfig} from "swr"

type RequestParams = {
    data: ProjectVersion,
    params: PageUrl,
}

type PageUrl = {
    version: string | null,
    project: string,
    page: string,
}

// provides `loaderData` to the component
export async function clientLoader({params}: Route.ClientLoaderArgs): Promise<RequestParams> {
    const requestUrl = createRequestUrl(params.versionProject, params.projectPage, params.page)
    if (!requestUrl) throw redirect("/404")

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
        .then(data => {
            return {data: data, params: requestUrl}
        })
        .catch(e => {
            throw redirect("/404")
        })
}

export default function Docs() {
    const request = useLoaderData<typeof clientLoader>()
    const navigate = useNavigate()

    const data = request.data
    const params = request.params

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
            <SWRConfig value={{
                dedupingInterval: 15000,
                fetcher: (url: string) => api.get(url).then(r => r.data),
                onError: (error) => {
                    navigate("/404")
                },
                shouldRetryOnError: false,
            }}>
                <Sidebar key="side-bar" project={data.project} name={data.name} document={data.document}/>
                <MainContent key="main-content" storageKeys={storageKeys} data={data} params={params}/>
            </SWRConfig>
        </div>
    </div>
}

function MainContent({storageKeys, data, params}: { storageKeys: StorageKeys, data: ProjectVersion, params: PageUrl }) {

    const document = data.document

    const buildToolsState = useConfiguration(storageKeys.buildTool, document.buildTools.map(value => BuildTools[value]))
    const languagesState = useConfiguration(storageKeys.language, document.languages.map(value => Languages[value]))
    const platformsState = useConfiguration(storageKeys.platform, document.platforms.map(value => Platforms[value]))

    return <Content
        key="content"
        version={data.version}
        page={params.page}
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
