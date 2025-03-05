import 'package:flutter/material.dart';
import 'package:frontend/src/providers/login_provider.dart';
import 'package:frontend/src/screens/login_screen.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => LoginProvider()),
      ],
      child: MaterialApp(
        home: const LoginScreen(),
        theme: ThemeData(primarySwatch: Colors.blue),
      ),
    ),
  );
}
