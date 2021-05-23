import React, {useState} from "react"
import ListItemText from "@material-ui/core/ListItemText"
import {createStyles, makeStyles, Theme, withStyles} from "@material-ui/core/styles"
import ListItem from "@material-ui/core/ListItem"
import Collapse from "@material-ui/core/Collapse"
import List from "@material-ui/core/List"
import {Entry} from "../../axios/Types"

interface BarDropdownProp {
  defaultOpen: boolean,
  text: string,
  child: Entry[]
}

export const BarDropdown: React.FC<BarDropdownProp> = ({defaultOpen, text, child}) => {
  const classes = useStyles()

  const [open, setOpen] = useState(defaultOpen)

  const handleClick = () => {
    setOpen(!open)
  }

  return (
      <div>
        <ListItem button onClick={handleClick}>
          <ListItemText primary={text}/>
          {open ? (<i className="fas fa-angle-down"/>) : (<i className="fas fa-angle-right"/>)}
        </ListItem>
        <Collapse className={classes.collapse} in={open} timeout="auto" unmountOnExit>
          <List component="div" disablePadding>
            {
              child.map(entry => {
                if (entry.type !== "LINK") {
                  return (<></>)
                }

                return (
                    <ListItem button key={entry.literal} className={classes.nested}>
                      <ListItemText primary={entry.literal}/>
                    </ListItem>
                )
              })
            }
          </List>
        </Collapse>
      </div>
  )
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
      nested: {
        paddingLeft: theme.spacing(4),
      },
      collapse: {
        margin: 0,
        display: "block",
        padding: 0,
        position: "relative",
        marginLeft: "16px",
        "&::before": {
          top: 0,
          left: 0,
          width: "2px",
          bottom: 0,
          content: `""`,
          position: "absolute",
          background: "rgb(35, 38, 39)",
        },
      },
    }),
)

export default BarDropdown