Slyce Android SDK "Camera Only" Example
=======================================

This sample code demonstrates how to levarage Slyce's Camera UI within your own application without requiring you to commit to a full UI integration.

# Getting Started
Download the latest official SDK release and copy it into your `app/libs` directory.  You can optionally replace this step with the Maven integration
process as described in the official [API Documentation](http://slyce-inc.github.io/Slyce-Android-SDK/).

<!-- official release -->
[![GitHub release](https://img.shields.io/github/release/Slyce-Inc/Slyce-Android-SDK.svg?style=flat-square)](https://github.com/Slyce-Inc/Slyce-Android-SDK/releases)

Paste your SDK credentials into the constants defined in `app/src/main/java/it/slyce/slyce_camera_only/main/viewcontroller/MainActivity.java`.

```
    private static final String SLYCE_ACCOUNT_ID = "";
    private static final String SLYCE_API_KEY = "";
    private static final String SLYCE_SPACE_ID = "";
```

You should now be able to run the sample application.  When the app first launches you will be presented with the Slyce Camera UI (which you can customize as needed).  All SDK
events, most notably when a search has been conducted and results returned, are exposed within `app/src/main/java/it/slyce/slyce_camera_only/custom/CustomUIFragment.java`.  This is
illustrates how you might add your own aplication behavior to the raw SDK results.

## Additional Support

For more information, be sure to check out the documentation [here](https://slyce.zendesk.com/) or from the official SDK repository [here](http://slyce-inc.github.io/Slyce-Android-SDK/)

---

Copyright Slyce, Inc 2014-2020
