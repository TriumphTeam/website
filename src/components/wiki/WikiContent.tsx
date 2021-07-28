import React, {useEffect, useState} from "react"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import {useParams} from "react-router-dom"
import api from "../axios/Api"

interface SideBarProp {
  url: string,
}

export const WikiContent: React.FC<SideBarProp> = ({url}) => {
  // CSS styles
  const classes = useStyles()
  const [content, setContent] = useState<string>("")

  // Simply for getting the current path url
  const {type, name, path} = useParams<{ type?: string, name?: string, path?: string }>()

  useEffect(() => {
    // Guarantees it's a valid type
    if (type !== "lib" && type !== "plugin") window.location.replace("/404")

    api.get("/page/" + name + "/" + path)
        .then(response => {
          setContent(response.data)
        })
        .catch(_ => {
          // TODO this might be temporary as i don't know if there is a better way to do this
          window.location.replace("/404")
        })
  }, [])

  // TODO move search bar to its own component
  return (
      <div dangerouslySetInnerHTML={{__html: content}}/>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({}),
)

export default WikiContent
