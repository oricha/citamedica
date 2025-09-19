
import { PrismaClient } from '@prisma/client';
import { NextResponse } from 'next/server';

const prisma = new PrismaClient();

// This endpoint is for independent doctors to register.
// It creates a pending registration for admin approval.
export async function POST(req: Request) {
  try {
    const { name, email, password, specialty, bio } = await req.json();

    if (!name || !email || !password || !specialty) {
      return NextResponse.json({ message: 'Missing required fields' }, { status: 400 });
    }

    const existingUser = await prisma.user.findUnique({
      where: { email },
    });

    if (existingUser) {
      return NextResponse.json({ message: 'An account with this email already exists.' }, { status: 409 });
    }
    
    const existingPending = await prisma.pendingRegistration.findFirst({
        where: { email: email, status: 'PENDING' }
    })

    if (existingPending) {
        return NextResponse.json({ message: 'A registration for this email is already pending approval.' }, { status: 409 });
    }

    await prisma.pendingRegistration.create({
      data: {
        type: 'DOCTOR',
        name: name,
        email: email,
        data: {
          name,
          email,
          password, // The password is saved to be used upon approval
          specialty,
          bio,
        },
      },
    });

    return NextResponse.json({ message: 'Doctor registration submitted and pending approval' }, { status: 201 });
  } catch (error) {
    console.error('Doctor registration error:', error);
    return NextResponse.json({ message: 'An internal error occurred.' }, { status: 500 });
  }
}
