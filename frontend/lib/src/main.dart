import 'package:flutter/material.dart';
import 'package:frontend/src/providers/auth_provider.dart';
import 'package:frontend/src/routes/routes.dart';
import 'package:frontend/src/screens/auth_screen.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthProvider()),
      ],
      child: MaterialApp(
        initialRoute: '/',  // Start with login screen
        onGenerateRoute: Routes.generateRoute,
        theme: ThemeData(primarySwatch: Colors.blue),
        debugShowCheckedModeBanner: false,
      ),
    ),
  );
}
