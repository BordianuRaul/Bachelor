import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/login_provider.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  LoginScreenState createState() => LoginScreenState();
}

class LoginScreenState extends State<LoginScreen> {
  bool isLogin = true;
  late FocusNode _emailFocusNode;
  late FocusNode _passwordFocusNode;

  @override
  void initState() {
    super.initState();
    _emailFocusNode = FocusNode();
    _passwordFocusNode = FocusNode();
  }

  @override
  void dispose() {
    _emailFocusNode.dispose();
    _passwordFocusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final loginProvider = Provider.of<LoginProvider>(context);

    return Scaffold(
      backgroundColor: Colors.black,
      body: Stack(
        children: [
          // Background with Blur
          Container(
            width: double.infinity,
            height: double.infinity,
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
          // Title & Subtitle at the top
          Positioned(
            top: MediaQuery.of(context).size.height * 0.2,  // Adjust position from top
            left: 0,
            right: 0,
            child: const Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // Title with dynamic scaling
                FittedBox(
                  fit: BoxFit.scaleDown,
                  child: Text(
                    'Let\'s begin...',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 30, // Adjust size as needed
                      fontWeight: FontWeight.bold,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ),
                SizedBox(height: 10),
                // Subtitle with dynamic scaling
                FittedBox(
                  fit: BoxFit.scaleDown,
                  child: Text(
                    'Login or Register to continue',
                    style: TextStyle(
                      color: Colors.white70,
                      fontSize: 18, // Adjust size as needed
                    ),
                    textAlign: TextAlign.center,
                  ),
                ),
              ],
            ),
          ),
          // Login Card UI
          Align(
            alignment: Alignment.bottomCenter,
            child: LayoutBuilder(
              builder: (context, constraints) {
                return SingleChildScrollView(
                  child: Container(
                    padding: const EdgeInsets.all(24),
                    decoration: const BoxDecoration(
                      color: Color(0xFFFFFFFF),
                      borderRadius: BorderRadius.only(
                        topLeft: Radius.circular(30),
                        topRight: Radius.circular(30),
                      ),
                    ),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        // Toggle Switch with Slide Animation
                        GestureDetector(
                          onHorizontalDragUpdate: (details) {
                            setState(() {
                              isLogin = details.localPosition.dx < constraints.maxWidth * 0.5;
                            });
                          },
                          onTap: () {
                            setState(() {
                              isLogin = !isLogin;
                            });
                          },
                          child: Container(
                            height: 70,
                            width: double.infinity,
                            decoration: BoxDecoration(
                              color: const Color(0xFFEEF0F2),
                              borderRadius: BorderRadius.circular(30),
                            ),
                            child: Stack(
                              children: [
                                AnimatedAlign(
                                  duration: const Duration(milliseconds: 300),
                                  alignment: isLogin ? Alignment.centerLeft : Alignment.centerRight,
                                  curve: Curves.easeInOut,
                                  child: Container(
                                    width: constraints.maxWidth * 0.5,
                                    height: 60,
                                    margin: const EdgeInsets.all(5),
                                    decoration: BoxDecoration(
                                      color: Colors.white,
                                      borderRadius: BorderRadius.circular(30),
                                      boxShadow: [
                                        BoxShadow(
                                          color: Colors.black.withOpacity(0.1),
                                          blurRadius: 5,
                                          spreadRadius: 1,
                                          offset: const Offset(0, 2),
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                                Row(
                                  children: [
                                    Expanded(
                                      child: GestureDetector(
                                        onTap: () {
                                          setState(() {
                                            isLogin = !isLogin;
                                          });
                                        },
                                        child: Center(
                                          child: Text(
                                            'Login',
                                            style: TextStyle(
                                              color: isLogin ? const Color(0xFF282828) : const Color(0xFFb8b8b8),
                                              fontWeight: FontWeight.bold,
                                              fontSize: 14,
                                            ),
                                          ),
                                        ),
                                      ),
                                    ),
                                    Expanded(
                                      child: GestureDetector(
                                        onTap: () {
                                          setState(() {
                                            isLogin = !isLogin;
                                          });
                                        },
                                        child: Center(
                                          child: Text(
                                            'Register',
                                            style: TextStyle(
                                              color: !isLogin ? const Color(0xFF282828) : const Color(0xFFb8b8b8),
                                              fontWeight: FontWeight.bold,
                                              fontSize: 14,
                                            ),
                                          ),
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        ),
                        const SizedBox(height: 20),
                        // Email Input
                        TextField(
                          controller: loginProvider.emailController,
                          focusNode: _emailFocusNode,
                          decoration: InputDecoration(
                            filled: true,
                            fillColor: Colors.white,
                            labelText: 'Email Address',
                            labelStyle: const TextStyle(color: Color(0xFFb8b8b8)),
                            enabledBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                              borderSide: const BorderSide(color: Color(0xFFF5F5F5)),
                            ),
                            focusedBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                              borderSide: const BorderSide(color: Color(0xFFF5F5F5)),
                            ),
                            prefixIcon: const Padding(
                              padding: EdgeInsets.all(12),
                              child: Icon(Icons.email, color: Color(0xFF6E947C), size: 32),
                            ),
                          ),
                          style: const TextStyle(color: Colors.black),
                        ),
                        const SizedBox(height: 15),
                        // Password Input
                        TextField(
                          controller: loginProvider.passwordController,
                          focusNode: _passwordFocusNode,
                          obscureText: true,
                          decoration: InputDecoration(
                            filled: true,
                            fillColor: Colors.white,
                            labelText: 'Password',
                            labelStyle: const TextStyle(color: Color(0xFFb8b8b8)),
                            enabledBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                              borderSide: const BorderSide(color: Color(0xFFF5F5F5)),
                            ),
                            focusedBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                              borderSide: const BorderSide(color: Color(0xFFF5F5F5)),
                            ),
                            prefixIcon: const Padding(
                              padding: EdgeInsets.all(12),
                              child: Icon(Icons.lock, color: Color(0xFF6E947C), size: 32),
                            ),
                          ),
                          style: const TextStyle(color: Colors.black),
                        ),
                        const SizedBox(height: 20),
                        // Pill-Shaped Login Button
                        ElevatedButton(
                          onPressed: () => loginProvider.login(context),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFF6E947C),
                            padding: const EdgeInsets.symmetric(vertical: 14),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(30),
                            ),
                            elevation: 4,
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
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
