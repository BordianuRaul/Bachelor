
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:frontend/src/services/auth_service.dart';

class RegisterProvider with ChangeNotifier {

  final AuthService _authService = AuthService();

  final TextEditingController firstNameController = TextEditingController();
  final TextEditingController lastNameController = TextEditingController();
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  bool _isLoading = false;

  bool get isLoading => _isLoading;

  Future<void> register(BuildContext context) async {
    final firstName = firstNameController.text;
    final lastName = lastNameController.text;
    final email = emailController.text;
    final password = passwordController.text;

    _isLoading = true;
    notifyListeners();

    try {
      await _authService.register(firstName, lastName, email, password);

      // Navigate to login screen after successful registration
      Navigator.pushReplacementNamed(context, '/login');

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Registration successful! Please log in.')),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Registration failed: ${e.toString()}')),
      );
    }

    _isLoading = false;
    notifyListeners();
  }

  @override
  void dispose() {
    firstNameController.dispose();
    lastNameController.dispose();
    emailController.dispose();
    passwordController.dispose();
    super.dispose();
  }
}