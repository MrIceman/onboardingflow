package com.nowocode.onboardingflow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nowocode.lib.OnboardingManager
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        OnboardingManager
            .instance(requireContext())
            .setActivity(requireActivity())
            .addOnboardingFeature(
                view.findViewById(R.id.textview_first),
                "Know your TextView",
                "By setting your custom text view data you can let your users know what" +
                        " the heck is going on with your UI :-)",
                VerticalPosition.BOTTOM
            )
            .addOnboardingFeature(
                requireActivity().findViewById<FloatingActionButton>(R.id.button_first),
                "Navigate to the next screen!",
                "By clicking that button you can skip this fragment and move to the first one!! How cool, huh?",
                VerticalPosition.TOP
            )
            .addOnboardingFeature(
                requireActivity().findViewById<FloatingActionButton>(R.id.fab),
                "Send an email!",
                "You can use this button to create a custom email and contact the developer. You can always rant about the app or just tell him a hi :-)",
                VerticalPosition.TOP
            )
            .addOnboardingFeature(
                requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(
                    R.id.toolbar
                ), "Oh, select this amazing toolBAR",
                "Not sure why you would highlight a toolbar, but with this library.. Let's say you can do it.",
                VerticalPosition.BOTTOM
            )
            .setFadeIn(true, 500L, 0f, 0.75f)
            .start()
    }
}