import 'package:flutter/material.dart';
import 'package:provider/provider.dart'; // Import the provider package
import 'package:frontend/src/providers/login_provider.dart';

class LoginScreen extends StatelessWidget {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final loginProvider = Provider.of<LoginProvider>(context);

    return Scaffold(
      appBar: AppBar(
        title: Text('Login'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            TextField(
              controller: loginProvider.emailController,
              decoration: InputDecoration(labelText: 'Email'),
            ),
            TextField(
              controller: loginProvider.passwordController,
              decoration: InputDecoration(labelText: 'Password'),
              obscureText: true,
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: () => loginProvider.login(context),
              child: Text('Login'),
            ),
          ],
        ),
      ),
    );
  }
}