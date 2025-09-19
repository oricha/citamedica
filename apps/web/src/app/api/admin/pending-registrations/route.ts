import { PrismaClient } from '@prisma/client';
import { NextResponse } from 'next/server';
import { getServerSession } from 'next-auth/next';
import { authOptions } from '../../auth/[...nextauth]/route';

const prisma = new PrismaClient();

export async function GET() {
  try {
    const session = await getServerSession(authOptions);
    if (!session || session.user.role !== 'SYSTEM_ADMIN') {
      return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
    }
    const regs = await prisma.pendingRegistration.findMany({ orderBy: { submittedAt: 'desc' } });
    return NextResponse.json({ data: regs });
  } catch (e: any) {
    return NextResponse.json({ error: e?.message || 'Error fetching registrations' }, { status: 500 });
  }
}

