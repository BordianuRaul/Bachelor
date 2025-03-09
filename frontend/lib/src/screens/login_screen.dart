import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:frontend/src/screens/register_screen.dart';
import 'package:provider/provider.dart';
import '../providers/login_provider.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  bool isLogin = true;

  @override
  Widget build(BuildContext context) {
    final loginProvider = Provider.of<LoginProvider>(context);

    return Scaffold(
      backgroundColor: Colors.black,
      body: LayoutBuilder(
        builder: (context, constraints) {
          double screenHeight = constraints.maxHeight;

          return Stack(
            children: [
              // Background with Blur
              SizedBox(
                height: screenHeight * 0.5,
                child: Stack(
                  children: [
                    Container(
                      decoration: const BoxDecoration(
                        image: DecorationImage(
                          image: AssetImage('assets/images/ui_background.jpeg'),
                          fit: BoxFit.cover,
                        ),
                      ),
                      child: BackdropFilter(
                        filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
                        child: Container(
                          color: Colors.black.withOpacity(0.5),
                        ),
                      ),
                    ),
                    // Title & Subtitle
                    Positioned(
                      top: screenHeight * 0.15,
                      left: 20,
                      right: 20,
                      child: Column(
                        children: const [
                          Text(
                            'Get Started with Ease',
                            style: TextStyle(
                              color: Colors.white,
                              fontSize: 28,
                              fontWeight: FontWeight.bold,
                            ),
                            textAlign: TextAlign.center,
                          ),
                          SizedBox(height: 10),
                          Text(
                            'Sign in or create an account to effortlessly manage your daily habits.',
                            style: TextStyle(
                              color: Colors.white70,
                              fontSize: 16,
                            ),
                            textAlign: TextAlign.center,
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              // Bottom Card UI
              Align(
                alignment: Alignment.bottomCenter,
                child: FractionallySizedBox(
                  heightFactor: 0.55,
                  child: Container(
                    padding: const EdgeInsets.all(24),
                    decoration: const BoxDecoration(
                      color: Color(0xFFFFFFFF), // White Background
                      borderRadius: BorderRadius.only(
                        topLeft: Radius.circular(30),
                        topRight: Radius.circular(30),
                      ),
                    ),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        // Toggle Switch with Animation
                        AnimatedSwitcher(
                          duration: const Duration(milliseconds: 300), // Duration for smooth transition
                          child: Container(
                            key: ValueKey<bool>(isLogin), // Key for AnimatedSwitcher to track state changes
                            height: 70, // Adjusted size
                            width: double.infinity,
                            decoration: BoxDecoration(
                              color: const Color(0xFFEEF0F2),
                              borderRadius: BorderRadius.circular(30),
                            ),
                            child: Row(
                              children: [
                                Expanded(
                                  child: GestureDetector(
                                    onTap: () => setState(() => isLogin = true),
                                    child: AnimatedContainer(
                                      duration: const Duration(milliseconds: 300),
                                      decoration: BoxDecoration(
                                        color: isLogin ? Colors.white : Colors.transparent,
                                        borderRadius: BorderRadius.circular(50),
                                        border: Border.all(
                                          color: isLogin ? const Color(0xFFeef0f2) : Colors.transparent,
                                          width: 4,
                                        ),
                                      ),
                                      alignment: Alignment.center,
                                      child: Text(
                                        'Login',
                                        style: TextStyle(
                                          color: isLogin ? const Color(0xFF282828) : const Color(0xFFb8b8b8),
                                          fontWeight: FontWeight.bold,
                                          fontSize: 14, // Smaller font
                                        ),
                                      ),
                                    ),
                                  ),
                                ),
                                Expanded(
                                  child: GestureDetector(
                                    onTap: () => setState(() => isLogin = false),
                                    child: AnimatedContainer(
                                      duration: const Duration(milliseconds: 300),
                                      decoration: BoxDecoration(
                                        color: isLogin ? Colors.transparent : Colors.white,
                                        borderRadius: BorderRadius.circular(50),
                                        border: Border.all(
                                          color: isLogin ? Colors.transparent : const Color(0xFFeef0f2),
                                          width: 4,
                                        ),
                                      ),
                                      alignment: Alignment.center,
                                      child: Text(
                                        'Register',
                                        style: TextStyle(
                                          color: isLogin ? const Color(0xFFb8b8b8) : const Color(0xFF282828),
                                          fontWeight: FontWeight.bold,
                                          fontSize: 14, // Smaller font
                                        ),
                                      ),
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),

                        const SizedBox(height: 20),
                        // Email Input
                        TextField(
                          controller: loginProvider.emailController,
                          decoration: InputDecoration(
                            filled: true,
                            fillColor: Colors.white, // White background
                            labelText: 'Email Address', // Label text that will float when typing
                            labelStyle: const TextStyle(color: Color(0xFFb8b8b8)), // Label color
                            enabledBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                              borderSide: const BorderSide(color: Color(0xFFF5F5F5)), // Light gray border
                            ),
                            focusedBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                              borderSide: const BorderSide(color: Color(0xFFF5F5F5)), // Same border when focused
                            ),
                            prefixIcon: Padding(
                              padding: const EdgeInsets.all(12),
                              child: Icon(Icons.email, color: Color(0xFF6E947C), size: 32), // Bigger icon
                            ),
                          ),
                          style: const TextStyle(color: Colors.black),
                        ),

                        const SizedBox(height: 15),
                        // Password Input
                        TextField(
                          controller: loginProvider.passwordController,
                          obscureText: true,
                          decoration: InputDecoration(
                            filled: true,
                            fillColor: Colors.white, // White background
                            labelText: 'Password', // Label text that will float when typing
                            labelStyle: const TextStyle(color: Color(0xFFb8b8b8)), // Label color
                            enabledBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                              borderSide: const BorderSide(color: Color(0xFFF5F5F5)), // Light gray border
                            ),
                            focusedBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                              borderSide: const BorderSide(color: Color(0xFFF5F5F5)), // Same border when focused
                            ),
                            prefixIcon: Padding(
                              padding: const EdgeInsets.all(12),
                              child: Icon(Icons.lock, color: Color(0xFF6E947C), size: 32), // Bigger icon
                            ),
                          ),
                          style: const TextStyle(color: Colors.black),
                        ),

                        const SizedBox(height: 20),
                        // Pill-Shaped Login Button
                        ElevatedButton(
                          onPressed: () => loginProvider.login(context),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFF6E947C), // Custom Green
                            padding: const EdgeInsets.symmetric(vertical: 14),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(30), // Fully rounded
                            ),
                            elevation: 4, // Added shadow
                          ),
                          child: const SizedBox(
                            width: double.infinity,
                            child: Text(
                              'Login',
                              textAlign: TextAlign.center,
                              style: TextStyle(fontSize: 18, color: Colors.white),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          );
        },
      ),
    );
  }
}
