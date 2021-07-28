import React from "react"
import {alpha, createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import Drawer from "@material-ui/core/Drawer"
import List from "@material-ui/core/List"
import InputBase from "@material-ui/core/InputBase"
import Toolbar from "@material-ui/core/Toolbar"
import {useParams} from "react-router-dom"

interface SideBarProp {
  url: string,
}

export const WikiContent: React.FC<SideBarProp> = ({url}) => {
  // CSS styles
  const classes = useStyles()

  // Simply for getting the current path url
  const {path} = useParams<{ type?: string, name?: string, path?: string }>()

  // TODO move search bar to its own component
  return (
      <>Hello</>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({

    }),
)

export default WikiContent
