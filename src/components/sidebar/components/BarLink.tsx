import React from "react"
import ListItemText from "@material-ui/core/ListItemText"
import ListItem from "@material-ui/core/ListItem"

type BarLinkProp = {
  text: string
  itemClass: string
  textClass: string
}

export const BarLink: React.FC<BarLinkProp> = ({text, itemClass, textClass}) => {

  return (
      <ListItem button className={itemClass} key={text}>
        <ListItemText className={textClass} primary={text}/>
      </ListItem>
  )
}

export default BarLink