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
- Set a schedule for an issue

## Usage

### Add a repository

1. Open a task window (Windows > Task)
2. Click add repository icon
3. Select the GitHub Issues Connector
4. Input display name, your user name, OAuth token and repository information
5. Click Connect button (If you can't connect a repository, please try to check input values)
6. Click OK button

## Default queries

1. Open
2. Assigned to me
3. Created by me

If you want to enable/disable these queries, Please change them on the Options panel.(Tools > Options > Team > GitHub Issues)

## GitHub OAuth Token

You can get a your OAuth token from the following: Settings > Applications > Personal access tokens > Generate new token

- Check `repo`
- Input token description
- Click generate token

## Resources

- [egit-github](https://github.com/eclipse/egit-github)

## License

[Common Development and Distribution License (CDDL) v1.0 and GNU General Public License (GPL) v2](http://netbeans.org/cddl-gplv2.html)
