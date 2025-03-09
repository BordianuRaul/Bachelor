import 'package:flutter/material.dart';
import 'package:frontend/src/providers/login_provider.dart';
import 'package:frontend/src/providers/register_provider.dart';
import 'package:frontend/src/routes/routes.dart';
import 'package:frontend/src/screens/login_screen.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => LoginProvider()),
        ChangeNotifierProvider(create: (_) => RegisterProvider())
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
