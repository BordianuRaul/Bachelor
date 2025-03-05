// lib/src/providers/login_provider.dart
import 'package:flutter/material.dart';

import '../services/auth_service.dart';

class LoginProvider with ChangeNotifier {
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  final AuthService _authService = AuthService();

  Future<void> login(BuildContext context) async {
    final email = emailController.text;
    final password = passwordController.text;

    try {
      await _authService.login(email, password);
      // Navigate to home screen on successful login
      Navigator.pushReplacementNamed(context, '/home');
    } catch (e) {
      // Show error message on login failure
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Login failed: ${e.toString()}')),
      );
    }
  }
}