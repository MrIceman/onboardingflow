# onboardingflow

<img src="onboardingflow-animation.gif" height="500" width="300"/>

A library that highlights UI Elements and adds a description to them. Basically, an onboarding flow to introduce the users to features / UI elements.

A library that allows you to highlight and describe any visible UI Element. <br/>
It is under development but can be already used / tested. The API is quite simple, call from your current Fragment / Activity below code

```kotlin

OnboardingManager
            .instance(requireContext())
            .setActivity(requireActivity())
            .addAction(
                OnboardingAction(
                    view = viewA,
                    text = "Some description what this view might be used for",
                    title = "A matching title",
                    VerticalPosition.BOTTOM  // Whether you want the description be displayed below or above the view
                )
            )
            .addAction(
                ...
            )
            // Set an initial fade-in animation
            .setFadeIn(
                    durationInMs = 300L, 
                    fromAlpha = 0f, 
                    toAlpha = 0.7f
            )
            // Execute code after user finished the onboarding
            .onDone {
                    myPrefs.onBoardingDone = true
            }
            // starts the Onboarding flow
            .start()
```
