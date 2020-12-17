package com.nowocode.onboardingflow

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.nowocode.lib.OnboardingManager
import com.nowocode.lib.ui.model.OnboardingAction
import com.nowocode.lib.ui.model.VerticalPosition

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        OnboardingManager
            .instance(requireContext())
            .setActivity(requireActivity())
            .addAction(
                OnboardingAction(
                    view.findViewById(R.id.textview_first),
                    "By setting your custom text view data you can let your users know what" +
                            " the heck is going on with your UI :-)",
                    "Know your TextView",
                    VerticalPosition.BOTTOM
                )
            )
            .addAction(
                OnboardingAction(
                    requireActivity().findViewById<FloatingActionButton>(R.id.button_first),
                    "By clicking that button you can skip this fragment and move to the first one!! How cool, huh?",
                    "Navigate to the next screen!",
                    VerticalPosition.TOP
                )
            )
            .addAction(
                OnboardingAction(
                    requireActivity().findViewById<FloatingActionButton>(R.id.fab),
                    "You can use this button to create a custom email and contact the developer. You can always rant about the app or just tell him a hi :-)",
                    "Send an email!",
                    VerticalPosition.TOP
                )
            )
            .addAction(
                OnboardingAction(
                    (requireActivity().findViewById<TabLayout>(
                        R.id.tabs
                    ) as TabLayout).getTabAt(1)?.view!!,
                    "This could be a description of what the user can find in Tab 2",
                    "Selected Tab 2",
                    VerticalPosition.BOTTOM
                )
            )
            .addAction(
                OnboardingAction(
                    (requireActivity().findViewById<TabLayout>(
                        R.id.tabs
                    ) as TabLayout).getTabAt(0)?.view!!,
                    "This could be a description of what the user can find in Tab 1",
                    "Selected Tab 1",
                    VerticalPosition.BOTTOM
                )
            )
            .setFadeIn(
                300L,
                0f,
                0.7f
            )
            .onDone {

            }
            .start()
    }
}