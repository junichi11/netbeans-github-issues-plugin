# NetBeans GitHub Issues Plugin

This plugin provides support for GitHub Issue Tracker.

## Features

- Create a new issue
- Edit an issue
- Create queries
- Edit an issue comment
- Delete an issue comment
- Search issues with issue number or keywords
- Create a new label
- Create a new milestone
- Add issues to schedule categories
- Insert and manage templates

## Usage

### Add a repository

1. Open a task window (Windows > Task)
2. Click add repository icon
3. Select the GitHub Issues Connector
4. Input display name, your user name, OAuth token and repository information
5. Click Connect button (If you can't connect a repository, please try to check input values)
6. Click OK button

### Schedules

If an issue has a milestone and it has a due date, the issue is added to schedule category.

### Insert and manage templates

You can insert a template and manage templates using buttons below the description.
All templates are used globally. (i.e. It isn't templates per repository.)

**NOTE:**You cannot remove the default template. If you remove it, it will be initilized.
You cannot edit a template name. So, if you want to change it, just remove it, then add a new template.

## Default queries

1. Open
2. Assigned to me
3. Created by me

If you want to enable/disable these queries, Please change them on the Options panel.(Tools > Options > Team > GitHub Issues)

## .github file

.github file can be set login name and oauth token. They are used as default values when we create a new repository. 
It must be put in the user home directory.
The format is the following:

```
login=junichi11
oauth=*****************************
```

## GitHub OAuth Token

You can get a your OAuth token from the following: Settings > Applications > Personal access tokens > Generate new token

- Check `repo`
- Input token description
- Click generate token

## Resources

- [egit-github](https://github.com/eclipse/egit-github)
- [octicons](https://octicons.github.com/)

## License

[Common Development and Distribution License (CDDL) v1.0 and GNU General Public License (GPL) v2](http://netbeans.org/cddl-gplv2.html)
